import { Component } from '@angular/core';
import { AppTitleService } from "./services/app-title.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  pageTitle: string = "";
  projectId: number = -1;

  constructor(private apptitleService: AppTitleService) {
  }

  ngOnInit() {
    this.apptitleService.getTitle().subscribe(title => {
        this.pageTitle = title;
        document.title = title;
      },
      e => console.log('on error %s', e),
      () => console.log('on complete'));
  }

  ngAfterViewInit() {
  }
}
