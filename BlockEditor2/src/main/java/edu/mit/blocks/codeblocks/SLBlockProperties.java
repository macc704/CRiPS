package edu.mit.blocks.codeblocks;

import edu.mit.blocks.workspace.Workspace;

public class SLBlockProperties {
//    public static final class RuntimeType<E> extends Enum<Enum<E>>{
//
//        public static RuntimeType[] values()
//        {
//            return (RuntimeType[])$VALUES.clone();
//        }
//
//        public static RuntimeType valueOf(String name)
//        {
//            return (RuntimeType)Enum.valueOf(slcodeblocks/SLBlockProperties$RuntimeType, name);
//        }
//
//        public String getString()
//        {
//            return myString;
//        }
//
//        public static RuntimeType getRuntime(String s)
//        {
//            RuntimeType arr$[] = values();
//            int len$ = arr$.length;
//            for(int i$ = 0; i$ < len$; i$++)
//            {
//                RuntimeType t = arr$[i$];
//                if(t.myString.equals(s))
//                    return t;
//            }
//
//            return NULL_RUNTIME;
//        }
//
//        public static final RuntimeType FOREVER;
//        public static final RuntimeType RUNONCE;
//        public static final RuntimeType RUNFORSOMETIME;
//        public static final RuntimeType NULL_RUNTIME;
//        private final String myString;
//        private static final RuntimeType $VALUES[];
//
//        static
//        {
//            FOREVER = new RuntimeType("FOREVER", 0, "forever");
//            RUNONCE = new RuntimeType("RUNONCE", 1, "runonce");
//            RUNFORSOMETIME = new RuntimeType("RUNFORSOMETIME", 2, "runforsometime");
//            NULL_RUNTIME = new RuntimeType("NULL_RUNTIME", 3, "");
//            $VALUES = (new RuntimeType[] {
//                FOREVER, RUNONCE, RUNFORSOMETIME, NULL_RUNTIME
//            });
//        }
//
//        private RuntimeType(String s1, int i, String s)
//        {
//            super(s1, i);
//            myString = s;
//        }
//    }


    public SLBlockProperties()
    {
    }

    public static boolean isCmd(String cmd, Block b)
    {
        return b != null && cmd.equals(b.getProperty("vm-cmd-name"));
    }

    public static boolean isMonitor(Block b)
    {
        return b != null && "monitor".equals(b.getProperty("stack-type"));
    }

    public static boolean isProcedureCall(Block b)
    {
        return b != null && "eval-procedure".equals(b.getProperty("vm-cmd-name")) && (b instanceof BlockStub);
    }

//    public static boolean isCollision(Long blockID)
//    {
//        return hasProperty(Block.getBlock(blockID), "is-collision");
//    }

    public static boolean hasProperty(Block b, String prop)
    {
        return b != null && b.getProperty(prop) != null;
    }

//    public static RuntimeType getRuntimeType(Block b)
//    {
//        return RuntimeType.getRuntime(b.getProperty("runtime-type"));
//    }

//    public static String getBreed(Block b)
//        throws CompilerException
//    {
//        String breed = b.getProperty("breed-name");
//        if(isCmd("nop", b))
//            breed = b.getBlockLabel().substring(7);
//        if(!BreedManager.isExistingBreed(breed))
//            throw new CompilerException(CompilerException.Error.CUSTOM, b.getBlockID(), (new StringBuilder()).append("Unrecognized breed: ").append(breed).toString());
//        else
//            return breed;
//    }

    public static int getInlineArg(Block b)
    {
        String inlineArg = b.getProperty("inline-arg");
        return inlineArg != null ? Integer.parseInt(inlineArg) : -1;
    }

//    public static Block getParent(Block b)
//    {
//        if(b instanceof BlockStub)
//        {
//            Block parent = ((BlockStub)b).getParent();
//            if(parent != null && RenderableBlock.getRenderableBlock(parent.getBlockID()).getParentWidget() != null)
//                return parent;
//        }
//        return null;
//    }

    public static Long getTopBlockID(Workspace ws, Long blockID)
    {
        if(blockID == null || Block.NULL.equals(blockID) || ws.getEnv().getBlock(blockID) == null)
            return null;
        Block b = ws.getEnv().getBlock(blockID);
        if(b.hasBeforeConnector())
            return getTopBlockID(ws, b.getBeforeBlockID());
        if(b.hasPlug())
            return getTopBlockID(ws, b.getPlugBlockID());
        if(b.isProcedureDeclBlock() || hasProperty(b, "is-collision"))
            return blockID;
        return null;
//        else
//            return isForeverRunBlock(blockID) ? blockID : null;
    }
//
//    public static boolean terminatesStack(Block b)
//    {
//        if(!b.getAfterBlockID().equals(Block.NULL))
//            return false;
//        Block before = Block.getBlock(b.getBeforeBlockID());
//        Long id = b.getBlockID();
//        for(; before != null; before = Block.getBlock(before.getBeforeBlockID()))
//        {
//            if(!before.getAfterBlockID().equals(id))
//                return terminatesStack(before);
//            id = before.getBlockID();
//        }
//
//        return true;
//    }

//    public static boolean isForeverRunBlock(Long blockID)
//    {
//        if(blockID.equals(Block.NULL))
//        {
//            return false;
//        } else
//        {
//            RuntimeType rt = getRuntimeType(Block.getBlock(blockID));
//            return rt == RuntimeType.FOREVER || rt == RuntimeType.RUNFORSOMETIME || rt == RuntimeType.RUNONCE;
//        }
//    }

    public static final String YES = "yes";
    public static final String VM_COMMAND_NAME = "vm-cmd-name";
    public static final String INLINE_ARG = "inline-arg";
    public static final String ASK_AGENT_ARG = "ask-arg";
    public static final String TYPE = "type";
    public static final String SCOPE = "scope";
    public static final String SCOPE_AGENT = "agent";
    public static final String SCOPE_GLOBAL = "global";
    public static final String SCOPE_PATCH = "patch";
    public static final String SCOPE_LOCAL = "local";
    public static final String BREED_NAME = "breed-name";
    public static final String INCLUDE_BREED = "include-breed";
    public static final String INCLUDE_BREED_SHAPE = "include-breed-shape";
    public static final String BOUNDING_MIN = "bounding-min";
    public static final String BOUNDING_MAX = "bounding-max";
    public static final String BOUNDING_VALUE = "bounding-value";
    public static final String STACK_TYPE = "stack-type";
    public static final String STACK_MONITOR = "monitor";
    public static final String STACK_SETUP = "setup";
    public static final String STACK_BREED = "breed";
    public static final String STACK_BREED_FOREVER = "breed-forever";
    public static final String SPECIAL_VAL = "special-value";
    public static final String KIND_CMD = "cmd";
    public static final String KIND_NUMBER = "number";
    public static final String KIND_STRING = "string";
    public static final String IS_MONITORABLE = "is-monitorable";
    public static final String IS_SETUP = "is-setup";
    public static final String IS_COLLISION = "is-collision";
    public static final String IS_MONITOR = "is-monitor";
    public static final String IS_SLIDER = "is-slider";
    public static final String IS_BAR_GRAPH = "is-bar-graph";
    public static final String IS_LINE_GRAPH = "is-line-graph";
    public static final String IS_TABLE = "is-table";
    public static final String IS_RUNTIME_BAR_GRAPH = "is-runtime-bar-graph";
    public static final String IS_RUNTIME_LINE_GRAPH = "is-runtime-line-graph";
    public static final String IS_RUNTIME_TABLE = "is-runtime-table";
    public static final String RUNTIME_TYPE = "runtime-type";
    public static final String HAS_RUNTIME_EQUIVALENT = "has-runtime-equiv";
    public static final String IS_SPECIAL_VAR = "is-special-variable";
    public static final String IS_OWNED_BY_BREED = "is-owned-by-breed";
    public static final String IS_BREED_SET_BY_CANVAS = "is-breed-set-by-canvas";
}
