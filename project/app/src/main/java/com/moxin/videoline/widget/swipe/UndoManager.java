package com.moxin.videoline.widget.swipe;

import android.view.View;

import java.util.LinkedList;

/**
 * Created by Paul Z on 2018/6/4.
 * Description: 卡片回退管理器
 */

public class UndoManager {
    LinkedList<UndoBean> undoList=new LinkedList<>();
    int MAX_STACK=2;
    UndoLinstener undoLinstener;
    public void add(View item,Object data){
        if(undoList.size()>=MAX_STACK){
            undoList.removeFirst();
        }
        UndoBean bin=new UndoBean(item,data);
        undoList.add(bin);
        if(undoLinstener!=null){
            undoLinstener.onStackChanged(undoList.size());
        }
    }

    public UndoBean remove(){
        if(undoList.size()==0){
            return null;
        }
        return undoList.removeLast();
    }

    public UndoBean undo(){
        UndoBean target=remove();
        if(target!=null&&undoLinstener!=null){
            undoLinstener.undo(target.data);
            undoLinstener.onStackChanged(undoList.size());
        }
        return target;
    }

    public void setUndoLinstener(UndoLinstener undoLinstener){
        this.undoLinstener=undoLinstener;
    }


    public interface UndoLinstener{
        public void undo(Object data);

        public void onStackChanged(int stackSize);
    }

    public static class UndoBean {
        public float x;
        public float y;
        public Object data;

        public UndoBean(View view, Object data){
            if (view==null){
                return;
            }
            this.data=data;
            this.x=view.getX();
            this.y=view.getY();
        }
    }
}
