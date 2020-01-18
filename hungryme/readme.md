#MongoDB
1) Install MongoDB.
2) Create database "hungry-me".
3) Create collection "recipes".
4) Change credentials in ```/src/main/java/ch/heigvd/mac/hungryme/Main.java```.
5) Run ```/src/main/java/ch/heigvd/mac/hungryme/Main.java```. It's successful if it display "Operation successful"

#Neo4j
1) find all the recipes having the tag breakfast (recipe, relation & tag): ```MATCH (n:Recipe)-[r]->(m:Tag {name : "breakfast"}) RETURN n,r,m```
2) delete all nodes: ```match (n) detach delete n```
3) research AND ```
MATCH (n:Recipe)--(:Ingredient{name:"milk"}),
                   (n)--(:Ingredient{name:"eggs"})
                   RETURN n```
4) other way for a AND research :
```
    MATCH (p:Recipe)-->(n:Ingredient)
    WHERE n.name IN ["sugar", "salt"]
    RETURN p
```
5) Get recipes based on ingredients ordered by ingredients found in recipe
````
UNWIND ["clove garlic", "milk", "salt"] AS ingredientName
MATCH (r:Recipe)-->(ingredient {name: ingredientName})
RETURN r.name, collect(ingredient.name) AS otherIngredients
ORDER BY size(collect(ingredient.name)) DESC
````

6) get Recipes by most likes or favorite
```
MATCH (a:User)-[l:looked]->(b:Recipe)
WHERE l.liked = true OR l.favorite = true
RETURN b, COLLECT(a) as users
ORDER BY SIZE(users) DESC
```