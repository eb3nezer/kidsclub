import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ProjectService } from "../../shared/services/project.service";
import { Project } from "../../shared/model/project";
import {ActivatedRoute, Router} from "@angular/router";
import {KcdocumentService} from "../../shared/services/kcdocument.service";
import {KCDocument} from "../../shared/model/kcDocument";

@Component({
  selector: 'view-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.css']
})
export class DocumentsComponent implements OnInit {
    project: Project;
    documents: KCDocument[];
    displayedColumns = ['icon', 'updated', 'file', 'description'];

    constructor(
        private appTitleService: AppTitleService,
        private route: ActivatedRoute,
        private router: Router,
        private projectService: ProjectService,
        private documentService: KcdocumentService
    ) {
    }

    loadProjectAndStudents() {
        const projectId = +this.route.snapshot.paramMap.get('projectId');
        if (projectId) {
            this.projectService.getProject(projectId).subscribe(project => {
                this.project = project;
                this.appTitleService.setCurrentProject(project);
            });

            this.documentService.getAllDocumentsForProject(projectId).subscribe(documents =>this.documents = documents);
        }
    }

    ngOnInit() {
    }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.appTitleService.setTitle("Documents"));
        Promise.resolve(null).then(() => this.loadProjectAndStudents());
    }
}
