local parse = require("kong.plugins.zipkin.request_tags").parse

describe("request_tags", function()
  describe("parse", function()
    it("parses simple case, swallowing spaces", function()
      assert.same({ foo = "bar" }, parse("foo=bar"))
      assert.same({ foo = "bar" }, parse("foo=  bar"))
      assert.same({ foo = "bar" }, parse("foo =bar"))
      assert.same({ foo = "bar" }, parse("foo  =  bar"))
      assert.same({ foo = "bar" }, parse("   foo =  bar"))
      assert.same({ foo = "bar" }, parse("foo = bar   "))
      assert.same({ foo = "bar" }, parse("  foo = bar   "))
    end)
    it("rejects invalid tags, keeping valid ones", function()
      local tags, err = parse("foobar;foo=;=bar;keep=true;=")
      assert.same(tags, {keep = "true"})
      assert.equals("Could not parse the following Zipkin tags: foobar, foo=, =bar, =", err)
    end)
    it("allows spaces on values, but not on keys", function()
      assert.same({ foo = "bar baz" }, parse("foo=bar baz"))
      local tags, err = parse("foo bar=baz")
      assert.same(tags, {})
      assert.equals("Could not parse the following Zipkin tags: foo bar=baz", err)
    end)
    it("parses multiple tags separated by semicolons, swallowing spaces", function()
      assert.same({ foo = "bar", baz = "qux" }, parse("foo=bar;baz=qux"))
      assert.same({ foo = "bar", baz = "qux" }, parse("foo  =bar;baz=qux"))
      assert.same({ foo = "bar", baz = "qux" }, parse("foo=  bar;baz=qux"))
      assert.same({ foo = "bar", baz = "qux" }, parse("  foo=bar  ;baz=qux"))
      assert.same({ foo = "bar", baz = "qux" }, parse("foo  =  bar  ;baz=qux"))
      assert.same({ foo = "bar", baz = "qux" }, parse("  foo  =  bar  ;baz=qux"))
      assert.same({ foo = "bar", baz = "qux" }, parse("  foo  =  bar  ;baz =qux"))
      assert.same({ foo = "bar", baz = "qux" }, parse("  foo  =  bar  ;baz  = qux"))
      assert.same({ foo = "bar", baz = "qux" }, parse("  foo  =  bar  ;baz  =  qux"))
    end)
    it("swallows empty tags between semicolons silently", function()
      local tags, err = parse(";;foo=bar;;;;baz=qux;;")
      assert.same({ foo = "bar", baz = "qux" }, tags)
      assert.is_nil(err)
    end)
    it("parses multiple tags separated by semicolons, in an array", function()
      assert.same({ foo = "bar", baz = "qux", quux = "quuz" },
                  parse({"foo=bar;baz=qux", "quux=quuz"}))
    end)
  end)
end)
