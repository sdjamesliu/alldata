local type = type
local kong = kong


local invalidate_cache = function(self, entity, options)
  local consumer = entity.consumer
  if type(consumer) ~= "table" then
    return true
  end

  -- skip next lines in some tests where kong cache is not available
  if not kong.cache then
    return true
  end

  local cache_key = self:cache_key(consumer.id)

  if options and options.no_broadcast_crud_event then
    return kong.cache:invalidate_local(cache_key)
  else
    return kong.cache:invalidate(cache_key)
  end
end


local _ACLs = {}


function _ACLs:post_crud_event(operation, entity, options)
  local _, err, err_t = invalidate_cache(self, entity, options)
  if err then
    return nil, err, err_t
  end

  return self.super.post_crud_event(self, operation, entity, options)
end


return _ACLs
