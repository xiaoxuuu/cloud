package cc.xiaoxu.cloud.core.utils.tree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>二叉树节点</p>
 *
 * @author 小徐
 * @since 2023/5/24 15:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}