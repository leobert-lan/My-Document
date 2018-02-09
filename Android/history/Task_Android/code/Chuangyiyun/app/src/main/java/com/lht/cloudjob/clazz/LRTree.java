package com.lht.cloudjob.clazz;

import com.lht.cloudjob.mvp.model.bean.CategoryResBean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.clazz
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> LRTree
 * <p><b>Description</b>: 使用左右节点号描述的树
 * 当数据量很大时，请使用异步
 * <p/>
 * 实质上我们需要描述一个森林，很操蛋！
 * 后端返回了一个森林的数据，我们放进来之后，假想有一个虚拟的根节点变成树，直接通过level查询出深度“1”的节点，即
 * 实际森林中每棵树的根节点，然后我们不需要重新构建森林（显然我也能写一个森林的类，当前已经足够用了），
 * 我们查询的时候，再按照树的root标记筛选一次即可。当然，综合的时空复杂度上升了。
 *
 * 如果写森林类，首先保证我们的这个tree足够单纯，把root相关的去掉。先建立森林，
 * 通过数据遍历将所有的数据按照root分组（分成树），然后分组数据建树
 * Created by leobert on 2016/8/4.
 */
public class LRTree {

    private final ArrayList<CategoryResBean> data;

    public LRTree(ArrayList<CategoryResBean> data) {
        this.data = data;
    }

    public ArrayList<CategoryResBean> querySon(CategoryResBean root) {
        ILRTreeQueryBuilder builder = newQueryBuilder(root.getRoot());
        builder.level(root.getLvl() + 1);
        builder.between(root.getLft(), root.getRgt());
        return builder.query();
    }

    public ArrayList<CategoryResBean> queryRoots() {
        ILRTreeQueryBuilder builder = new QueryBuilder(data);
        builder.level(0);
        return builder.query();
    }

    public ILRTreeQueryBuilder newQueryBuilder(int root) {
        return new QueryBuilder(data, root);
    }

    private class QueryBuilder implements ILRTreeQueryBuilder {

        private ArrayList<CategoryResBean> dumpData;

        private int root;

        private int lft = -1, rgt = -1;

        private int level = -1;

        QueryBuilder(ArrayList<CategoryResBean> data) {
            this(data,0);
        }

        QueryBuilder(ArrayList<CategoryResBean> data, int root) {
            check(root, "root");
            this.dumpData = data;
            this.root = root;
        }

        @Override
        public ILRTreeQueryBuilder between(int lft, int rgt) {
            check(lft, "lft");
            check(rgt, "rgt");
            this.lft = lft;
            this.rgt = rgt;
            return this;
        }

        @Override
        public ILRTreeQueryBuilder level(int level) {
            check(level, "level");
            this.level = level;
            return this;
        }

        @Override
        public ArrayList<CategoryResBean> query() {
            ArrayList<CategoryResBean> temp = findLevel(dumpData);
            temp = findInterval(temp);
            return temp;
        }

        private ArrayList<CategoryResBean> findLevel(ArrayList<CategoryResBean> data) {
            if (level >= 0) {
                ArrayList<CategoryResBean> temp = new ArrayList<>();
                for (CategoryResBean bean : data) {
                    if (bean.getLvl() == level) {
                        if (checkRoot(bean))
                            temp.add(bean);
                    }
                }
                return temp;
            }
            return data;
        }

        private ArrayList<CategoryResBean> findInterval(ArrayList<CategoryResBean> data) {
            if (lft >= 0 && rgt >= 0) {
                //设置了左右界限
                ArrayList<CategoryResBean> temp = new ArrayList<>();
                for (CategoryResBean bean : data) {
                    int l = bean.getLft();
                    int r = bean.getRgt();
                    if (lft < l && r < rgt) {
                        if (checkRoot(bean))
                            temp.add(bean);
                    }
                }
                return temp;
            } else if (rgt >= 0) {
                //设置了右界限
                ArrayList<CategoryResBean> temp = new ArrayList<>();
                for (CategoryResBean bean : data) {
                    int r = bean.getRgt();
                    if (r < rgt) {
                        if (checkRoot(bean))
                            temp.add(bean);
                    }
                }
                return temp;
            } else if (lft >= 0) {
                //设置了左边界
                ArrayList<CategoryResBean> temp = new ArrayList<>();
                for (CategoryResBean bean : data) {
                    int l = bean.getLft();
                    if (lft < l) {
                        if (checkRoot(bean))
                            temp.add(bean);
                    }
                }
                return temp;
            }
            //没有设置边界
            return data;
        }

        private void check(int natural, String name) {
            if (natural < 0)
                throw new IllegalArgumentException(name + " should >=0");
        }

        private boolean checkRoot(CategoryResBean bean) {
            if (root > 0) {
                return bean.getRoot() == root;
            }
            return true;
        }
    }


    /**
     * 复合查询
     */
    public interface ILRTreeQueryBuilder {
        /**
         * 指定leaf的左右界限，开区间
         * 从实际意义上来说，lft和rgt应该刚好是同一个节点的
         *
         * @param lft 左界限
         * @param rgt 右界限
         * @return 查询构造器
         */
        ILRTreeQueryBuilder between(int lft, int rgt);

        /**
         * 指定查询级别
         *
         * @param level 返回的leaf的级别
         * @return 查询构造器
         */
        ILRTreeQueryBuilder level(int level);

        ArrayList<CategoryResBean> query();
    }
}
