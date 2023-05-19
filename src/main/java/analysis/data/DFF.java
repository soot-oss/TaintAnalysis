package analysis.data;

import soot.Local;
import soot.SootField;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JArrayRef;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a Data Flow Fact, typically access paths
 */
public class DFF {

    private final static Logger LOGGER = Logger.getLogger(DFF.class.getName());

    private final Value value;

    private final Local base;

    // TODO: Check if list items are in correct order. E.g. 1st field's declaring class type must be the same as the base's type. 2nd field's declaring class type must be the same as the 1st fields tpype.
    private List<SootField> fields;

    private boolean isApproximated = false;

    private boolean invalid = false;

    private final Unit generatedAt;

    public DFF(Value value, Unit generatedAt) {
        this(value, generatedAt, false);
    }

    public DFF(Value base, Unit generatedAt, List<SootField> fields) {
        this(base, generatedAt);
        if (this.fields == null) {
            this.fields = new ArrayList<>();
        }
        this.fields.addAll(fields);
    }

    private DFF(Value value, Unit generatedAt, boolean isApproximated) {
        this.generatedAt = generatedAt;
        if (value instanceof Local) {
            base = (Local) value;
            fields = null;
        } else if (value instanceof InstanceFieldRef) {
            final InstanceFieldRef instanceField = (InstanceFieldRef) value;
            base = (Local) instanceField.getBase();
            fields = new ArrayList<>();
            fields.add(instanceField.getField());
        } else if (value instanceof StaticFieldRef) {
            final StaticFieldRef staticField = (StaticFieldRef) value;
            base = null;
            fields = new ArrayList<SootField>();
            fields.add(staticField.getField());
        } else if (value instanceof JArrayRef) {
            /**
             * We are modeling array indexes as access paths. Type of an index is array's type.
             * arr[0] -> arr.i_0
             * arr[10] -> arr.i_10
             */
            JArrayRef arr = (JArrayRef) value;
            base = (Local) (arr).getBase();
            fields = new ArrayList<>();
            SootField sootField = ArtificialObjectsCache.getSootField(arr);
            fields.add(sootField);
        } else {
            // only in case we compare to other values. E.g. when RHS is JNewExpression
            invalid = true;
            base = null;
        }
        this.value = value;
        this.isApproximated = isApproximated;
    }

    public Value getValue() {
        return value;
    }

    public Local getBase() {
        return base;
    }

    public List<SootField> getFields() {
        return fields;
    }

    public boolean isPublic() {
        if (fields != null) {
            return fields.get(fields.size() - 1).isPublic();
        } else {
            throw new NullPointerException();
        }
    }

    public Unit getGeneratedAt() {
        return generatedAt;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (base == null) {
            if (fields == null) {
                // this case happens when we convert something irrelevant e.g. JNewExpression or JNewArrayExpression to DFF only for comparison purposes
                return "irrelevant Object";
            }
            final SootField staticField = fields.get(0);
            sb.append(staticField.getDeclaringClass().getName());
            sb.append(".");
            sb.append(staticField.getName());
//            sb.append(": Static");
//            if (isPublic()) {
//                sb.append("_Public");
//            }
            return sb.toString();
        } else {
            sb.append(base.getName());
            if (fields != null) {
                final int length = fields.size();
                for (int i = 0; i < length; i++) {
                    sb.append(".");
                    sb.append(fields.get(i).getName());
                }
            }
            if (isApproximated) {
                sb.append(".*");
            }
            return sb.toString();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((base == null) ? 0 : base.hashCode());
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        result = prime * result + (isApproximated ? 0 : 1);
        result = prime * result + (invalid ? 0 : 1);
        return result;
    }

    public static DFF asDFF(Value val) {
        return new DFF(val, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final DFF other = (DFF) obj;
        if (isApproximated != other.isApproximated) return false;
        if (invalid != other.invalid) return false;
        if (base == null) {
            if (other.base != null) {
                return false;
            }
        } else if (!base.equals(other.base)) {
            return false;
        }
        if (fields == null) {
            if (other.fields != null) {
                return false;
            }
        } else {
            if (other.fields == null) {
                return false;
            }
            if (fields.size() != other.fields.size()) {
                return false;
            } else {
                for (int i = fields.size() - 1; i >= 0; i--) {
                    // field equality except number equality (field.getNumber())
                    // because we create artificial SootFields for array indices, and each one gets a new number
                    if (!fields.get(i).equals(other.fields.get(i))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if the given access path is a prefix of this access path.
     *
     * @param other the given access path
     * @return true, if the given access path is a prefix of this access path.
     */
    public boolean hasPrefix(DFF other) {
        if (other.invalid) {
            return false;
        }
        if (other.base == null && other.fields == null) {
            LOGGER.warning("other is not invalid but null");
        }
        if ((base != null && other.base != null) || (base == null && other.base == null)) {
            if (base != null) {
                // compare base
                if (!base.toString().equals(other.base.toString())) {
                    return false;
                }
            }
            // compare fields
            if (fields == null) {
                if (other.fields != null) {
                    return false;
                } else {
                    return true;
                }
            } else if (other.fields == null) {
                return true;
            } else if (fields.size() < other.fields.size()) {
                return false;
            } else {
                final int length = other.fields.size();
                for (int i = 0; i < length; i++) {
                    if (!fields.get(i).toString().equals(other.fields.get(i).toString())) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Internally checks whether other is prefix of this accessPath. If so, returns remainder of the access path.
     * E.g. this: a.b.c.d, other: a.b, returns c.d
     *
     * @param other
     * @return
     */
    public List<SootField> getRemainingFields(DFF other) {
        if (hasPrefix(other)) {
            List<SootField> remainingFields = new ArrayList<>();
            if (other.getFields() == null) {
                return fields;
            } else {
                int prefixLength = other.getFields().size();
                for (int i = prefixLength; i < fields.size(); i++) {
                    remainingFields.add(fields.get(i));
                }
                return remainingFields;
            }
        }
        return null;
    }


}
