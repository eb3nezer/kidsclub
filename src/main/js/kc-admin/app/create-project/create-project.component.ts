import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../services/app-title.service";

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent implements OnInit {

  constructor(private apptitleService: AppTitleService) { }

  ngOnInit() {
  }

  ngAfterViewInit() {
    Promise.resolve(null).then(() => this.apptitleService.setTitle("Create Project"));
  }
}
