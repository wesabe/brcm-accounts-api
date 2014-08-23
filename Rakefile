def mvn(*args)
  ensure_cmd("mvn")
  args.unshift("-o") if ENV["OFFLINE"] == "1"
  system("mvn", *args)
  return $?.exitstatus == 0
end

def mvn_or_die(*args)
  if !mvn(*args)
    yield if block_given?
    exit(-1)
  end
end

def cmd?(name)
  %x{which #{name}}
  return $?.exitstatus == 0
end

def ensure_cmd(name)
  if not cmd?(name)
    $stderr.puts "Unable to find command '#{name}'! Make sure it is installed and in your PATH (#{ENV['PATH']})"
    exit(-1)
  end
end

task :default => [:test]

desc "Runs the tests."
task :test do
  mvn_or_die("clean", "test") do
    for test_file in Dir["target/surefire-reports/*.txt"]
      test_results = File.read(test_file)
      if test_results =~ /FAILURE/m
        puts test_results
      end
    end
  end
end

namespace :test do
  desc "Generates an HTML coverage report and opens it in your default browser. NO_OPEN=1 to just generate the report."
  task :coverage do
    mvn_or_die("clean", "cobertura:cobertura")
    system("open", "target/site/cobertura/index.html") unless ENV["NO_OPEN"] == "1"
  end

  desc "Generates an HTML test report and opens it in your default browser. NO_OPEN=1 to just generate the report."
  task :report do
    mvn_or_die("clean", "surefire-report:report")
    system("open", "target/site/surefire-report.html") unless ENV["NO_OPEN"] == "1"
  end
end

def run_app(level, cmd)
  mvn(
    "-e",
    "-Djava.util.logging.config.file=src/test/resources/logging-#{level}.properties",
    "-Dexec.mainClass=com.wesabe.api.accounts.Runner",
    "-Dexec.args=#{cmd}",
    "clean", "compile", "exec:java"
  )
end

def run_server(level)
  run_app(level, "server --config=development.properties #{ENV['HOST'] && '--host=' + ENV['HOST']} --port=#{ENV['PORT'] || 8080}")
end

desc "Run brcm-accounts-api in an embedded Jetty server."
task :run do
  run_server(:normal)
end

namespace :run do
  desc "Run brcm-accounts-api in an embedded Jetty server with logging disabled."
  task :silent do
    run_server(:silent)
  end

  desc "Run brcm-accounts-api in an embedded Jetty server with logging turned up to 11."
  task :noisy do
    run_server(:noisy)
  end
end

namespace :db do
  desc "Generate a migration SQL DDL script."
  task :migration do
    run_app(:silent, "schema --config=development.properties --migration")
  end

  desc "Generate a SQL DDL schema."
  task :schema do
    run_app(:silent, "schema --config=development.properties")
  end
end
