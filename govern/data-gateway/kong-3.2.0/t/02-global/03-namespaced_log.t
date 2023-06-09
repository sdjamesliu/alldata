use strict;
use warnings FATAL => 'all';
use Test::Nginx::Socket::Lua;
do "./t/Util.pm";

no_long_string();

plan tests => repeat_each() * (blocks() * 3 + 4);

run_tests();

__DATA__

=== TEST 1: has core logging facility by default
--- http_config eval: $t::Util::HttpConfig
--- config
    location = /t {
        content_by_lua_block {
            local kong_global = require "kong.global"
            local kong = kong_global.new()
            kong_global.init_pdk(kong)

            kong.log.notice("hello world")
        }
    }
--- request
GET /t
--- ignore_response_body
--- error_log eval
qr/\[notice\] .*? \[kong\] content_by_lua\(nginx\.conf:\d+\):\d+ hello world/
--- no_error_log
[error]



=== TEST 2: set_namespaced_log() validates args
--- http_config eval: $t::Util::HttpConfig
--- config
    location = /t {
        content_by_lua_block {
            local kong_global = require "kong.global"
            local kong = kong_global.new()
            kong_global.init_pdk(kong)

            local pok, perr = pcall(kong_global.set_namespaced_log)
            if not pok then
                ngx.say(perr)
            end

            local pok, perr = pcall(kong_global.set_namespaced_log, kong)
            if not pok then
                ngx.say(perr)
            end
        }
    }
--- request
GET /t
--- response_body
arg #1 cannot be nil
namespace (arg #2) must be a string
--- no_error_log
[error]



=== TEST 3: reset_log() validates args
--- http_config eval: $t::Util::HttpConfig
--- config
    location = /t {
        content_by_lua_block {
            local kong_global = require "kong.global"
            local kong = kong_global.new()
            kong_global.init_pdk(kong)

            local pok, perr = pcall(kong_global.reset_log)
            if not pok then
                ngx.say(perr)
            end
        }
    }
--- request
GET /t
--- response_body
arg #1 cannot be nil
--- no_error_log
[error]



=== TEST 4: set_namespaced_log() switched to namespaced logging facility
--- http_config eval: $t::Util::HttpConfig
--- config
    location = /t {
        content_by_lua_block {
            local kong_global = require "kong.global"
            local kong = kong_global.new()
            kong_global.init_pdk(kong)

            kong_global.set_namespaced_log(kong, "my-plugin")

            kong.log.notice("hello world")
        }
    }
--- request
GET /t
--- ignore_response_body
--- error_log eval
qr/\[notice\] .*? \[kong\] content_by_lua\(nginx\.conf:\d+\):\d+ \[my-plugin\] hello world/
--- no_error_log
[error]



=== TEST 5: set_namespaced_log() + reset_log() swaps between logging facilities
--- http_config eval: $t::Util::HttpConfig
--- config
    location = /t {
        content_by_lua_block {
            local kong_global = require "kong.global"
            local kong = kong_global.new()
            kong_global.init_pdk(kong)

            kong_global.set_namespaced_log(kong, "my-plugin")
            kong.log.notice("hello world")

            kong_global.reset_log(kong)
            kong.log.notice("hello world")

            kong_global.set_namespaced_log(kong, "my-plugin")
            kong.log.notice("hello world")

            kong_global.set_namespaced_log(kong, "my-other-plugin")
            kong.log.notice("hello world")
        }
    }
--- request
GET /t
--- ignore_response_body
--- error_log eval
[
qr/\[notice\] .*? \[kong\] content_by_lua\(nginx\.conf:\d+\):\d+ \[my-plugin\] hello world/,
qr/\[notice\] .*? \[kong\] content_by_lua\(nginx\.conf:\d+\):\d+ hello world/,
qr/\[notice\] .*? \[kong\] content_by_lua\(nginx\.conf:\d+\):\d+ \[my-plugin\] hello world/,
qr/\[notice\] .*? \[kong\] content_by_lua\(nginx\.conf:\d+\):\d+ \[my-other-plugin\] hello world/
]
--- no_error_log
[error]



=== TEST 6: set_namespaced_log() produces light-thread safe namespaces
--- http_config
    init_by_lua_block {
        kong_global = require "kong.global"
        kong = kong_global.new()
        kong_global.init_pdk(kong)
    }
--- config
    location = /lua {
        content_by_lua_block {
            local args = assert(ngx.req.get_uri_args())
            local thread_name = args.name

            kong_global.set_namespaced_log(kong, thread_name)

            ngx.sleep(0.1)

            kong.log.notice(args.msg)
        }
    }

    location = /t {
        echo_subrequest_async GET '/lua' -q 'name=A&msg=hello';
        echo_subrequest_async GET '/lua' -q 'name=B&msg=world';
    }
--- request
GET /t
--- ignore_response_body
--- error_log eval
[
qr/\[notice\] .*? \[kong\] content_by_lua\(nginx\.conf:\d+\):\d+ \[A\] hello/,
qr/\[notice\] .*? \[kong\] content_by_lua\(nginx\.conf:\d+\):\d+ \[B\] world/,
]
--- no_error_log
[error]
