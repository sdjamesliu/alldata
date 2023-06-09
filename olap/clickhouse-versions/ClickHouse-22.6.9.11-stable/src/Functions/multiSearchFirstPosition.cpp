#include "FunctionsMultiStringSearch.h"
#include "FunctionFactory.h"
#include "MultiSearchFirstPositionImpl.h"
#include "PositionImpl.h"


namespace DB
{
namespace
{

struct NameMultiSearchFirstPosition
{
    static constexpr auto name = "multiSearchFirstPosition";
};

using FunctionMultiSearchFirstPosition
    = FunctionsMultiStringSearch<MultiSearchFirstPositionImpl<NameMultiSearchFirstPosition, PositionCaseSensitiveASCII>>;

}

void registerFunctionMultiSearchFirstPosition(FunctionFactory & factory)
{
    factory.registerFunction<FunctionMultiSearchFirstPosition>();
}

}
