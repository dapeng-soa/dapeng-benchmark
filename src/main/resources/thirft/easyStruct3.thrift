namespace java com.github.dapeng.benchmark.structs

include 'easyStruct1.thrift'

struct easyStruct3 {
    1 :  string v_string1,
    2 :  string v_string2,
    3 :  i32   v_i32,
    4 :  i64   v_i64,
    5 :  string v_string3,
    6 :  easyStruct1.easyStruct1 v_struct
}