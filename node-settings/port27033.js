rs.initiate({
   _id : "replica3",
   members: [ 
      { _id : 0, host : "xcnd12.comp.nus.edu.sg:27033" },
      { _id : 1, host : "xcnd13.comp.nus.edu.sg:27033" },
      { _id : 2, host : "xcnd14.comp.nus.edu.sg:27033" }
   ]
})