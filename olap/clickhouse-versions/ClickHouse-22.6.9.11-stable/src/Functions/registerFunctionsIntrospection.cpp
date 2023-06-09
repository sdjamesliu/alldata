namespace DB
{

class FunctionFactory;

#if defined(OS_LINUX)
void registerFunctionAddressToSymbol(FunctionFactory & factory);
void registerFunctionAddressToLine(FunctionFactory & factory);
void registerFunctionAddressToLineWithInlines(FunctionFactory & factory);
#endif

void registerFunctionDemangle(FunctionFactory & factory);
void registerFunctionTrap(FunctionFactory & factory);


void registerFunctionsIntrospection(FunctionFactory & factory)
{
#if defined(OS_LINUX)
    registerFunctionAddressToSymbol(factory);
    registerFunctionAddressToLine(factory);
    registerFunctionAddressToLineWithInlines(factory);
#endif
    registerFunctionDemangle(factory);
    registerFunctionTrap(factory);
}

}
