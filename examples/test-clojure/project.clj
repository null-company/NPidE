(defproject test-project "0.1.0-SNAPSHOT" 
    :dependencies [[org.clojure/clojure "1.10.3"], [org.clojure/tools.trace "0.7.9"], [debugger "0.2.1"]]
    :main ru.nsu.fit.core
    :profiles {:uberjar {:aot :all
    :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})