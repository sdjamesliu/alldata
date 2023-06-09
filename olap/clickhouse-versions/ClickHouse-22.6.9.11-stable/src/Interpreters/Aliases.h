#pragma once

#include <base/types.h>
#include <Parsers/IAST_fwd.h>

#include <unordered_map>

namespace DB
{

using Aliases = std::unordered_map<String, ASTPtr>;

}
