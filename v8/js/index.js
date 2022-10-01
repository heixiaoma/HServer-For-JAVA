function index() {
    console.log("被调用了")
    return "hello"
}
module.exports = {'test' : index};