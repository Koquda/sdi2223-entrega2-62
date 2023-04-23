module.exports  = function(app,logsRepository){



    app.get("/log/list", function(req,res){
        let logs = logsRepository.findAllLogs().then(function(logs){
            res.render("logs/list.twig",{logList:logs,session:req.session});
        });
    })

    app.get("/log/delete", function(req,res){
        logsRepository.deleteAllLogs().then(function (){
            res.redirect("/log/list");
        })
    })

    app.post("/log/filter",function (req,res){
        logsRepository.findAllByType(req.body.type).then(function(logs){
            res.render("logs/list.twig",{logList:logs,session:req.session});
        })
    })

}