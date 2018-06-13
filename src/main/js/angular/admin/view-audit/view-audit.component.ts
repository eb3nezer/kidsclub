import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../../shared/services/app-title.service";
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import {ProjectService} from "../../shared/services/project.service";
import {Project} from "../../shared/model/project";
import {AuditService} from "../../shared/services/audit.service";
import {AuditRecord} from "../../shared/model/auditRecord";

@Component({
  selector: 'app-view-audit',
  templateUrl: './view-audit.component.html',
  styleUrls: ['./view-audit.component.css']
})
export class ViewAuditComponent implements OnInit {
    project: Project;
    auditRecords: AuditRecord[];
    displayedColumns = ['auditEvent', 'auditTime', 'auditUser', 'auditProject'];

    constructor(private apptitleService: AppTitleService,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location,
              private projectService: ProjectService,
              private auditService: AuditService) {
      this.project = new Project();
  }

  ngOnInit() {
  }

  loadAudit() {
      var projectId = +this.route.snapshot.paramMap.get('id');
      if (!projectId) {
          this.projectService.getAllProjectsObservable().subscribe(projects => {
              projectId = projects[0].id;
              this.router.navigate([`/audit/${projectId}`]);
          });
      } else {
          this.projectService.getProjectObservable(projectId).subscribe(project => {
              this.project = project
          });
          this.auditService.getAuditRecordsForProject(projectId, 50, 0).subscribe(auditRecords => {
              this.auditRecords = auditRecords;
          });
      }
  }

    ngAfterViewInit() {
        Promise.resolve(null).then(() => this.apptitleService.setTitle("View audit log"));
        Promise.resolve(null).then(() => this.loadAudit());
    }
}
