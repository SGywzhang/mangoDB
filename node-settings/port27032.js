rs.initiate({
   _id : "replica2",
   members: [ 
      { _id : 0, host : "xcnd11.comp.nus.edu.sg:27032" },
      { _id : 1, host : "xcnd12.comp.nus.edu.sg:27032" },
      { _id : 2, host : "xcnd13.comp.nus.edu.sg:27032" }
   ]
})