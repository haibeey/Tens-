package com.sender.util

class Utils{
    companion object{
        fun <T> printItems(vararg ts: T){
            for (t in ts){
                print("$t-")
            }
            print("\n")
        }

        fun stripAudioName(name : String?):String{
            if (name==null)return  ""
            var dName = name
            val nameSplit = name.split(".")
            if (nameSplit.size>1){
                dName = nameSplit.subList(0,nameSplit.size-1).joinToString()
                if (dName.length>35){
                    dName = dName.substring(0,35)
                }else{
                    dName.padEnd(35,' ')
                }
            }
            return  dName
        }

        fun arrayListJoin(list : ArrayList<String>):String{
            var res = ""
            list.forEach { res+=it }
            return  res
        }

        fun nullOrEmptyString(string: String?):String{
            if (string==null)return  ""
            return string
        }
    }

}