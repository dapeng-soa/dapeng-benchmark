namespace java com.github.dapeng.benchmark.structs


include 'demoEnum.thrift'
include 'easyStruct1.thrift'
include 'easyStruct2.thrift'
include 'easyStruct3.thrift'
include 'simpleStruct.thrift'

struct complexStruct{
    1 : string v_string1,
    2 : string v_string2,
    3 : string v_string3,
    4 : i32 v_int1,
    5 : i32 v_int2,
    6 : i64 v_long1,
    7 : i64 v_long2,
    8 : i16 v_short1,
    9 : i16 v_short2,
    10 : list<string> v_string_list,
    11 : list<i32> v_int_list,
    12 : list<i64> v_long_list,
    13 : list<list<string>> v_list_list,
    14 : map<string, string> v_string_map,
    15 : map<string, i32> v_int_map,
    16 : map<i32,i32> v_int2_map,
    17 : map<string ,map<string,string>> v_map_map,
    18 : easyStruct1.easyStruct1 v_easyStruct1,
    19 : easyStruct2.easyStruct2 v_easyStruct2,
    20 : easyStruct3.easyStruct3 v_easyStruct3,
    21 : simpleStruct.simpleStruct v_simpleStruct1,
    22 : simpleStruct.simpleStruct v_simpleStruct2,
    24 : set<i32> v_int_set,
    25 : set<double> v_double_set,
    26 : list<map<string, string>> v_map_string_list,
    27 : list<map<string, i32>> v_map_int_list,
    28 : list<list<i32>> v_list_int_list,
    29 : list<list<i64>> v_list_long_list,
    30 : list<set<string>> v_string_set_list,
    31 : list<set<i32>> v_int_set_list,
    33 : list<set<i64>> v_long_set_list,
    34 : set<list<string>> v_string_list_set,
    35 : set<list<i32>> v_int_list_set,
    36 : set<list<double>> v_double_list_set,
    37 : easyStruct3.easyStruct3 v_easyStruct4,
    38 : easyStruct3.easyStruct3 v_easyStruct5,
    39 : easyStruct3.easyStruct3 v_easyStruct6,
    40 : easyStruct3.easyStruct3 v_easyStruct7,
    41 : easyStruct3.easyStruct3 v_easyStruct8,
    42 : easyStruct3.easyStruct3 v_easyStruct9,
    43 : easyStruct3.easyStruct3 v_easyStruct10,
    44 : easyStruct3.easyStruct3 v_easyStruct11,
    45 : simpleStruct.simpleStruct v_simpleStruct3,
    46 : simpleStruct.simpleStruct v_simpleStruct4,
    47 : simpleStruct.simpleStruct v_simpleStruct5,
    48 : list<list<map<string,string>>> v_map_list_list,
    49 : map<string, list<string>> v_string_list_map,
    50 : demoEnum.demoEnum v_enum
}