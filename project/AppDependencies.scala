import sbt.*

object AppDependencies {

  private val mongoHmrcVersion     = "2.12.0"
  private val playBootstrapVersion = "10.7.0"

  private lazy val compile = Seq(
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"            % "13.9.0",
    "uk.gov.hmrc"       %% "domain-play-30"                        % "11.0.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30" % "3.5.0",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"            % playBootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                    % mongoHmrcVersion
  )

  private lazy val test = Seq(
    "uk.gov.hmrc"          %% "bootstrap-test-play-30" % playBootstrapVersion,
    "org.scalatestplus"    %% "scalacheck-1-19"        % "3.2.20.0",
    "io.github.wolfendale" %% "scalacheck-gen-regexp"  % "1.1.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
