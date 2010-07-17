require "grit"

set :application, "brcm-accounts-api"
set :deploy_directory, "/opt/brcm-accounts-api"
set :current_jar, "current.jar"
set :remote_user, "brcm"
set :remote_group, "brcm"
set :timestamp, Time.now.strftime("%Y%m%d%H%M%S")
set :head, Grit::Repo.new(".").commits.first.sha[0..5]
set :jar_file, "#{application}-#{timestamp}-#{head}.jar"

def check_for_roles
  if roles.empty?
    logger.important("No environment specified. Use `cap staging CMD` or `cap production CMD`")
    exit(-1)
  end  
end

task :build do
  output = %x{ mvn clean package }
  if $?.exitstatus != 0
    STDERR.puts output
    STDERR.puts
    logger.important("Unable to build JAR. Try again when `mvn clean package` works.")
    exit(-1)
  end
  %x{ mv target/#{application}-*.jar target/#{jar_file} }
end

task :stage do
  temp_dir = "/tmp/#{application}.#{Array.new(6).map { rand(10) }.join("")}"
  run("mkdir -p #{temp_dir}")
  uploaded_jar_filename = "#{temp_dir}/#{jar_file}"
  local_jar_filename = "target/#{jar_file}"
  
  max_size = File.size(local_jar_filename).to_f
  last_percent = Hash.new { |h, k| h[k] = 0 }
  upload(local_jar_filename, uploaded_jar_filename, :via => :sftp) do |event, options, *details|
    if event == :put
      this_percent = ((details[1] / max_size) * 100).round
      if this_percent >= last_percent[options[:host]] + 5
        logger.debug("#{options[:host]}: #{this_percent}%")
        last_percent[options[:host]] = this_percent
      end
    end
  end
  
  sudo("mv #{uploaded_jar_filename} #{deploy_directory}/")
  sudo("chown #{remote_user}:#{remote_group} #{deploy_directory}/#{jar_file}")
end

task :install do
  sudo("ln -sf #{deploy_directory}/#{jar_file} #{deploy_directory}/#{current_jar}")
end

desc "Restart the application"
task :restart, :max_hosts => 1 do
  check_for_roles
  sudo("/etc/init.d/brcm-accounts-api restart")
end

desc "Scope all actions to the staging servers"
task :staging do
  role :app, "brcm1.stage"
end

desc "Scope all actions to the production servers"
task :production do
  role :app, "brcm1.prod", "brcm2.prod"
end

desc "Build and deploy the latest version of the application"
task :deploy do
  check_for_roles
  
  build
  stage
  install
  restart
end

desc "Remove all but the last five installed versions"
task :clean do
  files = []
  run("ls -A1 #{deploy_directory}") do |channel, stream, data|
    files += data.split("\n")
  end
  files.uniq!
  files -= [current_jar]
  files.sort!
  files -= files[-5..-1]
  
  sudo("rm -f #{files.map { |f| "#{deploy_directory}/#{f}" }.join(" ")}")
end