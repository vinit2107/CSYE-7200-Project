package ClusterConfiguration

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.elasticmapreduce.model._
import com.amazonaws.services.elasticmapreduce.util.StepFactory
import com.amazonaws.services.elasticmapreduce.{AmazonElasticMapReduceAsyncClient, AmazonElasticMapReduceAsyncClientBuilder}
import com.typesafe.config.Config

final case class jobInfo(clusterId: String, ticker: String)

object submitJob {
def apply()(implicit config: Config): Behavior[String] =
  Behaviors.setup(context => new submitJob(context))
}

class submitJob(context: ActorContext[String])(implicit config: Config) extends AbstractBehavior[String](context){
  override def onMessage(ticker: String): Behavior[String] = {
    val availableClusters = context.spawn(checkAvailableClusters(), "checkAvailability")
    availableClusters ! ticker
    Behaviors.empty
  }
}

object checkAvailableClusters {
  def apply()(implicit config: Config): Behavior[String] = {
    Behaviors.setup(context => new checkAvailableClusters(context))
  }
}

class checkAvailableClusters(context: ActorContext[String])(implicit config: Config) extends AbstractBehavior[String](context) {
  override def onMessage(ticker: String): Behavior[String] = {
    context.log.info("Checking if the cluster is running")
    val credentials_profile = new ProfileCredentialsProvider("default")
    val emrClusters = AmazonElasticMapReduceAsyncClient.asyncBuilder().withCredentials(credentials_profile).withRegion(Regions.US_EAST_1).build()
    val request = new ListClustersRequest().withClusterStates(ClusterState.WAITING, ClusterState.RUNNING, ClusterState.STARTING)
    val filteredClusters: ListClustersResult = emrClusters.listClusters(request)
    filteredClusters.getClusters.isEmpty match {
      case true => {
        val cc = context.spawn(configureEMR(), "configure-emr")
        cc ! ticker
        Behaviors.empty
      }
      case false => {
        val addStep = context.spawn(runJob(), "spark-job")
        addStep ! jobInfo(filteredClusters.getClusters.get(0).getId, ticker)
        Behaviors.empty
      }
    }
  }
}

object configureEMR {
  def apply()(implicit config: Config): Behavior[String] = {
    Behaviors.setup(context => new configureEMR(context))
  }
}

class configureEMR(context: ActorContext[String])(implicit config: Config) extends AbstractBehavior[String](context) {
  override def onMessage(ticker: String): Behavior[String] = {
    // Define Credentials
    val credentials_profile = new ProfileCredentialsProvider("default")
    // Create an EMR client using the credentials defined above
    val builder = AmazonElasticMapReduceAsyncClientBuilder.standard().withCredentials(credentials_profile).
      withRegion(Regions.US_EAST_1).build()
    // Create a step to enable debugging in AWS Management Console
    val stepFactory = new StepFactory()
    val stepConfig = new StepConfig().withName("Enable Debugging").withActionOnFailure("TERMINATE_JOB_FLOW").
      withHadoopJarStep(stepFactory.newEnableDebuggingStep())
    // Specify applications to be installed
    val spark = new Application().withName("Spark")
    // Cluster configuration request body
    val request = new RunJobFlowRequest().withName("Scion").withReleaseLabel("emr-6.0.0").withSteps(stepConfig).
      withApplications(spark).withLogUri(config.getString("emr.logURI")).withServiceRole("EMR_DefaultRole").
      withJobFlowRole("EMR_EC2_DefaultRole").withInstances(new JobFlowInstancesConfig().
      withEc2SubnetId(config.getString("emr.ec2SubnetId")).withEc2KeyName(config.getString("emr.ec2KeyName")).
      withInstanceCount(config.getInt("emr.instanceCount")).withKeepJobFlowAliveWhenNoSteps(true).
      withMasterInstanceType("m4.large").withSlaveInstanceType("m4.large"))
    // Request creating new Cluster
    val response = builder.runJobFlowAsync(request)
    val addStep = context.spawn(runJob(), "spark-job")
    addStep ! jobInfo(response.get().getJobFlowId, ticker)
    Behaviors.empty
  }
}

object runJob {
  def apply()(implicit config: Config): Behavior[jobInfo] = {
    Behaviors.setup(context => new runJob(context))
  }
}

class runJob(context: ActorContext[jobInfo])(implicit config: Config) extends AbstractBehavior[jobInfo](context) {
  override def onMessage(info: jobInfo): Behavior[jobInfo] = {
    // Define Credentials
    val credentials_profile = new ProfileCredentialsProvider("default")
    // Create an EMR client using the credentials defined above
    val emr = AmazonElasticMapReduceAsyncClientBuilder.standard().withCredentials(credentials_profile).
      withRegion(Regions.US_EAST_1).build()
    val sparkStepConf = new HadoopJarStepConfig().
      withJar("command-runner.jar").withArgs("spark-submit", "--jars", config.getString("emr.jarsPath"),
      "--files", config.getString("emr.filesPath"), "--class", config.getString("emr.classPath"),
      config.getString("emr.mainJarPath"), info.ticker)
    val sparkStep = new StepConfig().withName("Spark-Job").withActionOnFailure("CONTINUE").withHadoopJarStep(sparkStepConf)
    val request = new AddJobFlowStepsRequest().withJobFlowId(info.clusterId).withSteps(sparkStep)
    emr.addJobFlowStepsAsync(request)
    Behaviors.empty
  }
}
