local typedefs = require "kong.db.schema.typedefs"

return {
  name = "tcp-log",
  fields = {
    { protocols = typedefs.protocols },
    { config = {
        type = "record",
        fields = {
          { host = typedefs.host({ required = true }), },
          { port = typedefs.port({ required = true }), },
          { timeout = { type = "number", default = 10000 }, },
          { keepalive = { type = "number", default = 60000 }, },
          { tls = { type = "boolean", required = true, default = false }, },
          { tls_sni = { type = "string" }, },
          { custom_fields_by_lua = typedefs.lua_code },
        },
    }, },
  }
}
