(ns fr.jeremyschoffen.mbt2.utils)

(defn transfer-doc* [src dest]
  (println src (resolve src))
  (println dest (resolve dest))
  (let [doc (-> src resolve meta (select-keys [:doc :arglists]))]
    (println doc)
    (-> dest
        resolve
        (alter-meta! merge doc))))

(defmacro transfer-doc [src dest]
  `(transfer-doc* ~src ~dest))
