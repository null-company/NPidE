package file_representation

class Line<T> {
    var nodes: ArrayList<Node<T>>

    constructor(nodes: ArrayList<Node<T>>) {
        this.nodes = nodes;
    }
}