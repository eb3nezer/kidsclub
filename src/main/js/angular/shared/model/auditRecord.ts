import { User } from "./user";
import { Project } from "./project";

export class AuditRecord {
    constructor(public id: number,
                public change: string,
                public changeTime: string,
                public user: User,
                public auditLevel: string,
                public project: Project,
                public created: number,
                public updated: number
                ) {
    }
}
