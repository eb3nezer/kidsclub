import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportStudentsComponent } from './import-students.component';

describe('ImportStudentsComponent', () => {
  let component: ImportStudentsComponent;
  let fixture: ComponentFixture<ImportStudentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ImportStudentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportStudentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
