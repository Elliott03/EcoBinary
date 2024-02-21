package main

import (
	"net/http"
	"os/exec"

	"github.com/gin-gonic/gin"
)

type executor struct {
	Commands []string `json:"commands"`
}

func main() {
	router := gin.Default()
	router.POST("/execute", executeCommand)
	router.GET("/test", testGet)
	router.Run(":8080")
}

func testGet(c *gin.Context) {
	c.IndentedJSON(http.StatusOK, "ok")
}

func executeCommand(c *gin.Context) {
	var executor executor
	if err := c.BindJSON(&executor); err != nil {
		return
	}
	for _, cmd := range executor.Commands {
		println(cmd)
	}
	cmdString := ""
	for _, command := range executor.Commands {
		cmdString += command + " && "
	}
	cmdString = cmdString[:len(cmdString)-4]
	//println(cmdString)
	cmd := exec.Command("/bin/sh", "-c", cmdString)

	output, err := cmd.CombinedOutput()
	if err != nil {
		println("error")
		return
	}
	//println(string(output))
	c.IndentedJSON(http.StatusOK, string(output))
}
