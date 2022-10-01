var test = require('./test.js');
console.log(test.test())
for (var i = 0; i < 10; i++) {
    console.log(test.test())
}

setInterval(()=>{
    console.log(javaFunction())
},1000)