
class Test2 {
    void update(Map<String, String> populatedProperties) {

        //Sure there is a better way to write this, but this is business logic, let's make it easier to read for non-programmers.

        if (populatedProperties["mail"]) {
            populatedProperties["o365_mail"] = populatedProperties["mail"]
        } else {
            if (populatedProperties["test"]) {
                populatedProperties["o365_mail"] = populatedProperties["test"]
            }
        }
    }
}
