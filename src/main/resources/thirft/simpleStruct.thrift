namespace java com.github.dapeng.benchmark.structs


include 'easyStruct1.thrift'
include 'easyStruct2.thrift'
include 'easyStruct3.thrift'

struct simpleStruct {
    1 :  string v_string1,
    2 :  string v_string2,
    3 :  i32   v_int1,
    4 :  i32   v_int2,
    5 :  easyStruct1.easyStruct1 v_struct1,
    6 :  easyStruct2.easyStruct2 v_struct2,
    7 :  list<string> v_list,
    8 :  map<string,map<string,i32>> v_map,
    9 :  easyStruct3.easyStruct3 v_struct3,
    10 : bool v_bool
}