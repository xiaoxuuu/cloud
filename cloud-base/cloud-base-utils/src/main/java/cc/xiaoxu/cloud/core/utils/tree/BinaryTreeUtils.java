package cc.xiaoxu.cloud.core.utils.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>二叉树工具类，需继承 {@link BinaryTreeNode BinaryTreeNode}</p>
 *
 * @author 小徐
 * @since 2023/5/24 15:40
 */
public class BinaryTreeUtils {

    /**
     * 禁止实例化
     */
    private BinaryTreeUtils() {
        throw new IllegalAccessError("XBinaryTreeUtils.class");
    }

    /**
     * <p>获取以某节点为子树的高度</p>
     * <p>求二叉树的高度：</p>
     * <p>首先要一种获取以某个节点为子树的高度的方法，使用递归调用。</p>
     * <p>如果一个节点为空，那么这个节点肯定是一颗空树，高度为 0；</p>
     * <p>如果不为空，那么我们要遍历地比较它的左子树高度和右子树高度，高的一个为这个子树的最大高度，然后加上自己本身的高度就是了</p>
     * <p>获取二叉树的高度，只需要调用第一种方法，即传入根节点</p>
     *
     * @param node 节点
     * @param <T>  数据类型
     * @return 高度
     */
    public static <T> int height(BinaryTreeNode<T> node) {

        if (node == null) {
            // 递归结束，空子树高度为 0
            return 0;
        } else {
            // 递归获取左子树高度
            int l = height(node.getLeft());
            // 递归获取右子树高度
            int r = height(node.getRight());
            // 高度应该算更高的一边，（+1 是因为要算上自身这一层）
            return l > r ? (l + 1) : (r + 1);
        }
    }

    /**
     * <p>求二叉树的节点数：</p>
     * <p>求节点数时，我们看看获取某个节点为子树的节点数的实现。</p>
     * <p>首先节点为空，则个数肯定为 0；</p>
     * <p>如果不为空，那就算上这个节点之后继续递归所有左右子树的子节点数，</p>
     * <p>全部相加就是以所给节点为根的子树的节点数</p>
     * <p>如果求二叉树的节点数，则输入根节点即可</p>
     *
     * @param node 节点
     * @param <T>  节点类型
     * @return 节点总数
     */
    public static <T> int size(BinaryTreeNode<T> node) {

        if (node == null) {
            // 如果节点为空，则返回节点数为 0
            return 0;
        } else {
            // 计算本节点 所以要 +1
            // 递归获取左子树节点数和右子树节点数，最终相加
            return 1 + size(node.getLeft()) + size(node.getRight());
        }
    }

    /**
     * 集合转树，逐层排列，null 视为空节点，且此空节点对应子节点位置会被跳过
     *
     * @param list 集合
     * @param <T>  集合元素类型
     * @return 树
     */
    public static <T> BinaryTreeNode<T> toTree(List<T> list) {

        int index = 0;
        LinkedList<BinaryTreeNode<T>> linkedList = new LinkedList<>();
        linkedList.add(new BinaryTreeNode<>());
        while (index < list.size()) {

            BinaryTreeNode<T> node = linkedList.poll();
            if (node == null) {
                break;
            }
            node.setData(list.get(index));
            if ((index + 1) * 2 - 1 < list.size()) {
                node.setRight(new BinaryTreeNode<>());
                linkedList.add(node.getLeft());
            }
            if ((index + 1) * 2 < list.size()) {
                node.setRight(new BinaryTreeNode<>());
                linkedList.add(node.getRight());
            }
            index++;
        }
        return new BinaryTreeNode<>();
    }

    /**
     * TODO 树转集合
     *
     * @param tree 树
     * @param <T>  树元素类型
     * @return 集合
     */
    @Deprecated
    public static <T> List<T> toList(BinaryTreeNode<T> tree) {

        return new ArrayList<>();
    }
}