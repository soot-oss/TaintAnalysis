# TaintAnalysis
This is a field sensitive taint analysis client implementation on top of Heros, 
which uses Boomerang to resolve aliasing.

## Dependencies
Following dependencies must be built to run the analysis.  
- Heros: https://github.com/Sable/heros  
- BoomerangPDS: https://github.com/CodeShield-Security/SPDS

## How to run
- Various test cases are listed under `test/target/taint`  
- Run the test cases in `TaintAnalysisTest`  
- sources and sinks are defined as `SootMethodRef`'s in `createAnalysisTransformer` method.