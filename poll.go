package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"time"
)

const (
	//URL = "http://echov2.herokuapp.com/end"
	URL = "http://localhost:5000/end"
)

func main() {
	for {
		time.Sleep(2 * time.Second)
		if hasEnded() {
			return
		}
	}
}

type State struct {
	Str      string `json:"str"`
	Complete bool   `json:"complete"`
}

func hasEnded() bool {
	r, err := http.Get(URL)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Got error: %s\n", err)
		return true
	}
	defer r.Body.Close()
	appState := State{}
	if err := json.NewDecoder(r.Body).Decode(&appState); err != nil {
		fmt.Fprintf(os.Stderr, "Got error: %s\n", err)
		return true
	}
	if appState.Complete {
		fmt.Println(appState.Str)
	}
	return appState.Complete
}
