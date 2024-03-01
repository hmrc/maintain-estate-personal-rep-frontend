import sbt.*

object AppDependencies {

  private val mongoHmrcVersion = "1.3.0"
  private val playBootstrapVersion = "7.21.0"

  private lazy val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"         %% "play-frontend-hmrc-play-28"     % "8.5.0",
    "uk.gov.hmrc"         %% "domain"                         % "8.3.0-play-28",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"         %% "bootstrap-frontend-play-28"     % playBootstrapVersion,
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-play-28"             % mongoHmrcVersion
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"         % playBootstrapVersion,
    "org.scalatest"          %% "scalatest"                      % "3.2.18",
    "org.scalatestplus"      %% "mockito-4-11"                   % "3.2.18.0",
    "org.scalatestplus"      %% "scalacheck-1-17"                % "3.2.18.0",
    "com.vladsch.flexmark"   % "flexmark-all"                    % "0.64.8",
    "org.jsoup"              %  "jsoup"                          % "1.17.2",
    "org.wiremock"           % "wiremock-standalone"             % "3.4.2",
    "io.github.wolfendale"   %% "scalacheck-gen-regexp"          % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
