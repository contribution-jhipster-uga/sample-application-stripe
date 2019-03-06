/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { StripeTestModule } from '../../../test.module';
import { PaymentUpdateComponent } from 'app/entities/payment/payment-update.component';
import { PaymentService } from 'app/entities/payment/payment.service';
import { Payment } from 'app/shared/model/payment.model';

describe('Component Tests', () => {
    describe('Payment Management Update Component', () => {
        let comp: PaymentUpdateComponent;
        let fixture: ComponentFixture<PaymentUpdateComponent>;
        let service: PaymentService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [StripeTestModule],
                declarations: [PaymentUpdateComponent]
            })
                .overrideTemplate(PaymentUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(PaymentUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PaymentService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Payment(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.payment = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Payment();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.payment = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
