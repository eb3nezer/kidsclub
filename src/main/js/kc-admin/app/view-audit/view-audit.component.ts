import { Component, OnInit } from '@angular/core';
import { ApptitleService } from "../apptitle.service";

@Component({
  selector: 'app-view-audit',
  templateUrl: './view-audit.component.html',
  styleUrls: ['./view-audit.component.css']
})
export class ViewAuditComponent implements OnInit {

  constructor(private apptitleService: ApptitleService) { }

  ngOnInit() {
  }

  ngAfterViewInit() {
    Promise.resolve(null).then(() => this.apptitleService.setTitle("View audit log"));
  }
}
