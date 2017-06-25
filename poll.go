package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"time"
)

var (
	URL string
)

func main() {
	URL = os.Args[1] + "end"
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
