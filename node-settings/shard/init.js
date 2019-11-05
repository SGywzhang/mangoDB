sh.addShard("replica1/xcnd10.comp.nus.edu.sg:27031,xcnd11.comp.nus.edu.sg:27031,xcnd12.comp.nus.edu.sg:27031")

sh.addShard("replica2/xcnd11.comp.nus.edu.sg:27032,xcnd12.comp.nus.edu.sg:27032,xcnd13.comp.nus.edu.sg:27032")

sh.addShard("replica3/xcnd12.comp.nus.edu.sg:27033,xcnd13.comp.nus.edu.sg:27033,xcnd14.comp.nus.edu.sg:27033")

sh.addShard("replica4/xcnd10.comp.nus.edu.sg:27034,xcnd13.comp.nus.edu.sg:27034,xcnd14.comp.nus.edu.sg:27034")

sh.addShard("replica5/xcnd10.comp.nus.edu.sg:27035,xcnd11.comp.nus.edu.sg:27035,xcnd14.comp.nus.edu.sg:27035")

sh.enableSharding("cs4224c")

sh.shardCollection("cs4224c.customer", { "_id" : "hashed" } )
sh.shardCollection("cs4224c.district", { "_id" : "hashed" } )
sh.shardCollection("cs4224c.orderItem", { "_id" : "hashed" } )
sh.shardCollection("cs4224c.stock", { "_id" : "hashed" } )