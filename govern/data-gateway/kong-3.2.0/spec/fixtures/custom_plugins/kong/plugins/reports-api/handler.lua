local ReportsApiHandler = {
  PRIORITY = 1000,
  VERSION = "1.0",
}

function ReportsApiHandler:preread()
  local reports = require "kong.reports"
  reports._sync_counter()
  reports.send_ping()
  ngx.print("ok")
  ngx.exit(200)
end


return ReportsApiHandler
