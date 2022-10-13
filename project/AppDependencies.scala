import sbt._
import play.core.PlayVersion

object AppDependencies {

  private val mongoHmrcVersion = "0.73.0"
  private val playBootstrapVersion = "7.8.0"

  private lazy val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"         %% "play-frontend-hmrc"             % "1.1.0-play-28",
    "uk.gov.hmrc"         %% "domain"                         % "8.1.0-play-28",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping"  % "1.12.0-play-28",
    "uk.gov.hmrc"         %% "bootstrap-frontend-play-28"     % playBootstrapVersion,
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-play-28"             % mongoHmrcVersion
  )

  private lazy val test = Seq(
    "org.scalatest"          %% "scalatest"             % "3.2.14",
    "org.scalatestplus"      %% "mockito-4-6"           % "3.2.14.0",
    "org.scalatestplus"      %% "scalacheck-1-17"       % "3.2.14.0",
    "org.scalatestplus.play" %% "scalatestplus-play"    % "5.1.0",
    "com.vladsch.flexmark"   % "flexmark-all"           % "0.62.2",
    "org.jsoup"              %  "jsoup"                 % "1.15.3",
    "com.typesafe.play"      %% "play-test"             % PlayVersion.current,
    "com.github.tomakehurst" % "wiremock-standalone"    % "2.27.2",
    "io.github.wolfendale"   %% "scalacheck-gen-regexp" % "1.0.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
