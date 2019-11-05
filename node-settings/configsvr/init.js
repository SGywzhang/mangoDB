rs.initiate(
  {
    _id: "configsrvreplica",
    configsvr: true,
    members: [
      { _id : 0, host : "xcnd10.comp.nus.edu.sg:27001" },
      { _id : 1, host : "xcnd12.comp.nus.edu.sg:27001" },
      { _id : 2, host : "xcnd14.comp.nus.edu.sg:27001" }
    ]
  }
)