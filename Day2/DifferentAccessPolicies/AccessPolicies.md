| Feature              | **ABAC (Attribute-Based Access Control)** | **RBAC (Role-Based Access Control)** | **MAC (Mandatory Access Control)** | **DAC (Discretionary Access Control)** |
|----------------------|------------------------------------------|--------------------------------------|-------------------------------------|-------------------------------------|
| **Access Control Basis** | Attributes (user, resource, environment) | Roles assigned to users | Security labels/classifications | Owner-defined permissions |
| **Policy Flexibility** | Highly flexible | Moderate | Strict and rigid | Less flexible |
| **Granularity** | Very fine-grained | Coarse to moderate | High | Moderate |
| **Who Controls Access?** | Policy engine evaluates attributes | Admin assigns roles | Central authority (e.g., government, military) | Resource owner (creator) |
| **Use Case Examples** | Cloud security, complex enterprise systems | Enterprise applications, HR systems | Government, military, classified data | File-sharing systems, personal OS security |
| **Ease of Management** | Complex (requires detailed policies) | Easier (role hierarchy simplifies management) | Complex (strict control, hard to change) | Easy (users set access, but lacks oversight) |
| **Security Level** | High (dynamic and context-aware) | Moderate (depends on role design) | Very high (strict enforcement) | Low to moderate (prone to insider threats) |
| **Scalability** | High (handles large, dynamic environments well) | High (well-structured roles) | Low (hard to scale due to rigid rules) | Low (individual permissions can be chaotic) |
| **Common in** | Cloud services, Zero Trust security | Enterprise IT, ERP systems | Government, military | Traditional OS (Windows, Linux file systems) |

### Summary:
- **ABAC** is **dynamic and highly flexible** but requires detailed policies.
- **RBAC** is **structured and scalable** but lacks fine-grained control.
- **MAC** is **very secure** but **strict and inflexible**.
- **DAC** is **simple and easy** but **prone to security risks**.
