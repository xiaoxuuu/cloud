package cc.xiaoxu.cloud.core.utils.tree;

/**
 * <p>二叉树节点</p>
 *
 * @author 小徐
 * @since 2023/5/24 15:24
 */
public class BinaryTreeNode<T> {

    /**
     * 数据
     */
    private T data;

    /**
     * 左节点
     */
    private BinaryTreeNode<T> left;

    /**
     * 右节点
     */
    private BinaryTreeNode<T> right;

    public BinaryTreeNode() {
    }

    public BinaryTreeNode(T data, BinaryTreeNode<T> left, BinaryTreeNode<T> right) {
        this.data = data;
        this.left = left;
        this.right = right;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BinaryTreeNode<T> getLeft() {
        return left;
    }

    public void setLeft(BinaryTreeNode<T> left) {
        this.left = left;
    }

    public BinaryTreeNode<T> getRight() {
        return right;
    }

    public void setRight(BinaryTreeNode<T> right) {
        this.right = right;
    }
}