namespace java com.github.dapeng.benchmark.structs

include 'demoEnum.thrift'
include 'easyStruct1.thrift'

struct allStruct{
    1 :  bool  v_bool,
    2 :  i16   v_i16,
    3 :  i32   v_i32,
    4 :  i64   v_i64,
    5 :  double v_double,
    6 :  string v_string,
    7 :  easyStruct1.easyStruct1 v_struct,
    8 :  map<string,string> v_map,
    9 :  set<string> v_set,
    10 : list<string> v_list,
    11 : demoEnum.demoEnum v_enum
}