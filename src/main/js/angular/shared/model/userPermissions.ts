import {User} from "./user";
import {Project} from "./project";
import {PermissionRecord} from "./permissionRecord";

export class UserPermissions {
    constructor(
        public user?: User,
        public project?: Project,
        public userSitePermissions?: PermissionRecord[],
        public userProjectPermissions?: PermissionRecord[]
    ) {
    }
}
