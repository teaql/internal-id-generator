package io.teaql.internalidgenerator;


import cn.hutool.core.map.MapUtil;
import io.teaql.data.EntityStatus;
import io.teaql.data.TQLContext;
import io.teaql.data.UserContext;
import io.teaql.data.meta.EntityMetaFactory;
import io.teaql.data.sql.SQLRepositorySchemaHelper;
import io.teaql.idspace.Q;
import io.teaql.idspace.idspace.IdSpace;
import io.teaql.idspace.userdomain.UserDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller

public class IdGeneratorController {
    @Autowired
    private EntityMetaFactory factory;
    @GetMapping("/ensureDB")
    @ResponseBody
    public Object ensureTable(@TQLContext UserContext context) {

        new SQLRepositorySchemaHelper().ensureSchema(context, factory);
        return MapUtil.of("ok", true);
    }
    @GetMapping("/init")
    @ResponseBody
    public Object init(@TQLContext UserContext context) {

        UserDomain d = new UserDomain().updateName("ID SPACE");
        d.save(context);

        return MapUtil.of("ok", true);
    }

    @PostMapping("/genId")
    @ResponseBody
    public Object genId(@TQLContext UserContext context, @RequestBody  IdGenerationRequest request) {


        IdSpace space=Q.idSpace().returnType(IdSpaceX.class).orderByCurrentDescending().execute(context);



        if(space==null){
            IdSpace newSpace=new IdSpaceX().updateCurrent(1);
            newSpace.updateInitDigitsLength(6);
            newSpace.updateTypeName(request.getTypeName());
            newSpace.updateDomain(Q.userDomain().executeForList(context).first());
            newSpace.save(context);

            return newSpace.updateDomain(null);
        }
        space.set$status(EntityStatus.UPDATED);
        ((IdSpaceX)space).increase().save(context);
        return space.updateDomain(null);
    }




    //IdGenerationRequest



}


/*


package teaql.demo;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.doublechaintech.todo.TodoUserContext;
import com.doublechaintech.todo.merchant.Merchant;
import com.doublechaintech.todo.todoitem.TodoItem;
import io.teaql.data.TQLContext;
import io.teaql.data.UserContext;
import io.teaql.data.meta.EntityMetaFactory;
import io.teaql.data.sql.SQLRepositorySchemaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.doublechaintech.todo.Q;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DemoController {

  @Autowired private EntityMetaFactory factory;

  @GetMapping("/ensureDB")
  @ResponseBody
  public Object ensureTable(@TQLContext UserContext context) {
    new SQLRepositorySchemaHelper().ensureSchema(context, factory);
    return MapUtil.of("ok", true);
  }

  @GetMapping("/search")
  @ResponseBody
  public Object query(@TQLContext TodoUserContext context)  {



    return Q.merchant().selectPlatform().executeForList(context);
  }
  @GetMapping("/search2")
  @ResponseBody
  public Object query2(@TQLContext TodoUserContext context) {
    return Q.todoItem().executeForList(context);
  }

  @GetMapping("/new/{name}/")
  @ResponseBody
  public Object newTodo(@TQLContext TodoUserContext context,@PathVariable("name") String name)
          throws Exception {


    TodoItem todoItem=new TodoItem().updateName(name);



    todoItem.updateStatus("TODO");
    todoItem.updateMerchant(Q.merchant().executeForList(context).first());
    return todoItem.save(context);

  }
  @GetMapping("/newM/{name}/")
  @ResponseBody
  public Object newM(@TQLContext TodoUserContext context,@PathVariable("name") String name)
          throws Exception {


    Merchant merchant=new Merchant().updateName("双链科技");

    return merchant.save(context);

  }
  @GetMapping("/get/")
  @ResponseBody
  public Object get(@TQLContext TodoUserContext context) {
    return Q.merchant().findWithJsonExpr("{\"platform.name\":\"平台\"}").executeForList(context);
  }

  @GetMapping("/get1/")
  @ResponseBody
  public Object get1(@TQLContext TodoUserContext context) {
    return Q.merchant().findWithJsonExpr("{\"todoItemList.name\":\"T1\"}").executeForList(context);
  }

  @GetMapping("/todolist/")
  @ResponseBody
  public Object todolist(@TQLContext TodoUserContext context) {
    return Q.todoItem().executeForList(context);
  }


  @GetMapping("/q/")
  @ResponseBody
  public Object q(@TQLContext TodoUserContext context) {

    Object o1=Q.merchant()
            .selectTodoItemList()
            .findWithJsonExpr("{\"todoItemList.name\":\"T5\"}").executeForList(context);

    Object o2=Q.todoItem().executeForList(context);

    Object o3=Q.merchant().filterById(1657255672659062784L)
            .selectTodoItemList()
            .countTodoItems()
            //.countTodoItems("countTodoItems001")
            .executeForList(context);


    Map map=new MapBuilder<>(new HashMap<>())
            .put("o1",o1)
            .put("o2",o2)
            .put("o3",o3)
            .build();


    return map;
  }




  @GetMapping("/find/")
  @ResponseBody
  public Object find(@TQLContext TodoUserContext context, @RequestBody String load) {
    return Q.merchant().findWithJsonExpr(load).executeForList(context);
  }



}


*
* */