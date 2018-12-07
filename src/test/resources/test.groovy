
class Test {
    void update(Map<String, String> populatedProperties) {
        populatedProperties["groovy"] = "test"

        populatedProperties["groovy2"] = populatedProperties["test"] + populatedProperties["test"]

        System.out.println "Count: " + populatedProperties.size()
    }
}
