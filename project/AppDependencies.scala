import sbt.*

object AppDependencies {

  private val mongoHmrcVersion = "1.9.0"
  private val playBootstrapVersion = "8.5.0"

  private lazy val compile = Seq(
    "uk.gov.hmrc"         %% "play-frontend-hmrc-play-30"             % "8.5.0",
    "uk.gov.hmrc"         %% "domain-play-30"                         % "9.0.0",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping-play-30"  % "2.0.0",
    "uk.gov.hmrc"         %% "bootstrap-frontend-play-30"             % playBootstrapVersion,
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-play-30"                     % mongoHmrcVersion
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"         % playBootstrapVersion,
    "org.scalatestplus"      %% "scalacheck-1-17"                % "3.2.18.0",
    "io.github.wolfendale"   %% "scalacheck-gen-regexp"          % "1.1.0",
    "org.jsoup"              %  "jsoup"                          % "1.17.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
