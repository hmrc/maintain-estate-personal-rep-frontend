import uk.gov.hmrc.DefaultBuildSettings.targetJvm

ThisBuild / scalaVersion := "3.9.0"
ThisBuild / targetJvm := "jvm-21"
ThisBuild / majorVersion := 1

lazy val appName: String = "maintain-estate-personal-rep-frontend"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    routesImport += "models.*",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat.*",
      "uk.gov.hmrc.govukfrontend.views.html.components.*",
      "uk.gov.hmrc.hmrcfrontend.views.html.components.*",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers.*",
      "views.ViewUtils.*",
      "models.Mode",
      "controllers.routes.*"
    ),
    PlayKeys.playDefaultPort := 8829,
    CodeCoverageSettings(),
    scalacOptions ++= Seq(
      "-Wconf:src=routes/.*:s",
      "-Wconf:msg=unused import&src=html/.*:s",
      "-feature"
    ),
    Test / javaOptions ++= Seq(
      "-Dconfig.resource=test.application.conf"
    ),
    libraryDependencies ++= AppDependencies()
  )

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
