rs.initiate({
   _id : "replica1",
   members: [ 
      { _id : 0, host : "xcnd10.comp.nus.edu.sg:27031" },
      { _id : 1, host : "xcnd11.comp.nus.edu.sg:27031" },
      { _id : 2, host : "xcnd12.comp.nus.edu.sg:27031" }
   ]
})